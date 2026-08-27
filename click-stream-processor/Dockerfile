# Build stage runs on the native build machine (x86_64). $BUILDPLATFORM is
# injected by BuildKit — keeps this stage off QEMU.
FROM --platform=$BUILDPLATFORM golang:1.25-alpine AS builder

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

# CGO_ENABLED=0 produces a fully static binary that requires no libc —
# required for distroless/static.
# amd64 everywhere since the arm64 OCI Ampere box is gone. Azure Container Apps
# is amd64-only, and building arm64 is what made the earlier direct-to-App-Service
# attempt fail: the image started and exited immediately. BuildKit overrides these
# when --platform is passed.
ARG TARGETOS=linux
ARG TARGETARCH=amd64
RUN CGO_ENABLED=0 GOOS=${TARGETOS} GOARCH=${TARGETARCH} \
    go build -ldflags="-w -s" -o /app/server ./...

# Final stage: distroless/static has no shell, no package manager, no libc.
# nonroot variant runs as uid 65532 out of the box.
FROM gcr.io/distroless/static-debian12:nonroot

WORKDIR /app
COPY --from=builder /app/server /app/server
# schema.avsc is read at runtime via os.ReadFile("schema.avsc") (relative path)
COPY --from=builder /app/schema.avsc /app/schema.avsc

EXPOSE 8080
ENTRYPOINT ["/app/server"]
