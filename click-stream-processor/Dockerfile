# Build stage runs on the native Cloud Build machine (x86_64).
# $BUILDPLATFORM is injected by BuildKit — keeps this stage off QEMU.
FROM --platform=$BUILDPLATFORM golang:1.23-alpine AS builder

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

# BuildKit populates TARGETOS/TARGETARCH from the --platform flag passed by
# Cloud Build (linux/arm64). CGO_ENABLED=0 produces a fully static binary
# that requires no libc — required for distroless/static.
ARG TARGETOS=linux
ARG TARGETARCH=arm64
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
