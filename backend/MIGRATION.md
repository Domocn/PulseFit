# Railway → Hetzner migration runbook

Backend: FastAPI + uvicorn, MongoDB via `motor`, Firebase ID-token auth,
ElevenLabs TTS. Single stateless container — no local disk state.

---

## Step 0 — Check this first, it decides everything

Open the Railway dashboard and read the **`MONGO_URL`** variable on the service.

| What it points at | What migration means |
|---|---|
| `mongodb+srv://…mongodb.net` (**Atlas**) | Nothing to migrate. The database stays exactly where it is; only the app moves. Add the Hetzner IP to the Atlas IP allowlist and you're done. |
| A Railway MongoDB plugin (internal host) | The data has to move too. Dump and restore before cutover — see Step 5b. |

`.env.example` suggests Atlas, but the deployed value is the authority. Everything below assumes Atlas; Step 5b covers the other case.

---

## Step 1 — Order the box

At [console.hetzner.com](https://console.hetzner.com) → **Add Server**:

- **Location:** Falkenstein or Helsinki (~20–30 ms from the UK)
- **Image:** Ubuntu 24.04
- **Type:** **CAX21** — 4 vCPU / 8 GB / 80 GB, Arm64
- **Networking:** IPv4 + IPv6
- **Backups:** enable (+20%). You have no redundancy; this is the whole recovery plan.
- **SSH key:** add your public key. Do not use password auth.

Verify the live monthly price in the console — Hetzner raised prices on 15 June 2026 and the ~€12 figure is arithmetic off their hourly rate, not a quote.

**Arm64 caveat:** every dependency in the slimmed `requirements.txt` ships aarch64 wheels. The one to watch is `grpcio` (via `firebase-admin`) — if pip starts compiling it from source the build will take 10+ minutes rather than failing. If that happens, either accept the slow first build (it caches) or move to a CPX box.

---

## Step 2 — Harden the host

```bash
ssh root@<server-ip>

apt update && apt upgrade -y
apt install -y ufw fail2ban

ufw default deny incoming
ufw default allow outgoing
ufw allow OpenSSH
ufw allow 80/tcp
ufw allow 443/tcp
ufw enable

systemctl enable --now fail2ban

# unattended security updates
apt install -y unattended-upgrades
dpkg-reconfigure -plow unattended-upgrades
```

Disable password logins in `/etc/ssh/sshd_config` (`PasswordAuthentication no`), then `systemctl restart ssh`.

---

## Step 3 — Install Docker

```bash
curl -fsSL https://get.docker.com | sh
docker --version && docker compose version
```

---

## Step 4 — Point DNS

Create an **A record** for your API hostname → the server's IPv4, and an **AAAA** → its IPv6.
Set TTL to **300 s** *now*, before cutover — it makes rollback fast.

Wait for propagation before starting Caddy, or certificate issuance will fail:

```bash
dig +short api.yourdomain.com
```

---

## Step 5 — Deploy

```bash
git clone https://github.com/Domocn/PulseFit.git /opt/pulsefit
cd /opt/pulsefit/backend
```

Edit `Caddyfile` — replace `api.example.com` with your real hostname.

Create `.env` next to `docker-compose.yml` (mode `600`, never committed):

```ini
MONGO_URL=mongodb+srv://…            # copied from Railway
DB_NAME=pulsefit
ELEVENLABS_API_KEY=…                 # copied from Railway
ALLOWED_ORIGINS=https://yourdomain.com   # NOT *
```

Copy the Firebase service-account JSON to `backend/firebase-service-account.json` (mode `600`). Then:

```bash
chmod 600 .env firebase-service-account.json
docker compose up -d --build
docker compose logs -f api
```

### Step 5b — only if MongoDB was a Railway plugin

Before cutover, with the Railway service still running:

```bash
mongodump --uri="<railway-internal-mongo-url>" --out=./dump
mongorestore --uri="<new-mongo-url>" --drop ./dump
```

Do this during a quiet window, and re-run it immediately before flipping DNS so you don't lose writes made in between.

---

## Step 6 — Verify before you cut over

Run all of these against the new host and don't skip the third.

```bash
# health
curl -s https://api.yourdomain.com/api/health

# resident memory — this is the number the whole exercise is about
docker stats --no-stream

# image size, for comparison against the old ~4.4 GB
docker images | grep pulsefit
```

**Confirm Firebase auth is actually on.** `server.py:51` wraps
`firebase_admin.initialize_app()` in a bare `try/except` that sets
`FIREBASE_AUTH_ENABLED = False` and logs a warning. If credentials are missing
the app boots perfectly happily **with authentication disabled** — a silent,
wide-open API. Check for this line in the logs:

```
Firebase Admin SDK not configured - auth middleware disabled
```

If you see it, stop and fix the credentials before touching DNS. Then confirm a
request without a valid bearer token is rejected:

```bash
curl -s -o /dev/null -w '%{http_code}\n' https://api.yourdomain.com/api/<an-authed-route>
# expect 401/403, NOT 200
```

---

## Step 7 — Cut over

1. Lower the DNS TTL (done in Step 4) and confirm it has taken effect.
2. Repoint the A/AAAA records at Hetzner.
3. Watch `docker compose logs -f` and the Atlas connection count.
4. Leave the Railway service **running but idle for ~48 h** — it costs a few dollars and is your rollback. Reverting is one DNS change.
5. Once stable, delete the Railway service. Note the bill is charged for usage already incurred, so cancelling does not clear the outstanding $40.16.

---

## Step 8 — Afterwards

- **Backups:** Hetzner snapshots cover the box; they do **not** cover Atlas. Enable Atlas backups separately.
- **Uptime:** add an external monitor on `/api/health` — nothing on this box will tell you it's down.
- **Updates:** `unattended-upgrades` handles the OS. Container updates are yours: `docker compose pull && docker compose up -d --build`.
- **Egress:** before launch, move APK/media delivery to Cloudflare R2 and 302 to it rather than streaming through FastAPI. That keeps launch traffic off this box entirely.

---

## What changed in the repo

| File | Change |
|---|---|
| `requirements.txt` | 127 pinned packages → 11 direct deps. Removed `pandas`, `numpy`, `litellm`, `openai`, three Google GenAI SDKs, `boto3`, the HuggingFace stack, `stripe`, `pillow` — all verified as zero-reference across every `.py` file. |
| `requirements.railway-freeze.txt` | The original freeze, preserved unchanged for reference. |
| `requirements-dev.txt` | New. `pytest`, `requests`, `black`, `flake8`, `isort`, `mypy` — never enters the image. |
| `Dockerfile` | Added `MALLOC_ARENA_MAX=2`, non-root user, healthcheck, explicit `--workers 1`. |
| `.dockerignore` | New. Keeps tests, caches, git history and `.env` out of the build context. |
| `docker-compose.yml` | New. API + Caddy, `mem_limit: 1g` as a regression tripwire. |
| `Caddyfile` | New. Automatic TLS, security headers, log rotation. |

Nothing has been committed — `git diff` to review, and note the two edited files
(`requirements.txt`, `Dockerfile`) are the only pre-existing ones touched.
