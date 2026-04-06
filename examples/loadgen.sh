#!/usr/bin/env bash
# loadgen.sh - continuously calls all example endpoints and prints span IDs.
# Usage: ./loadgen.sh [n]
#   n  Fibonacci input (default: 40)
#
# The otel-agent_cprary example returns the span ID in the HTTP response.
# The otel-extension and otel-extension-cp-start examples do not (the OTel
# agent loads in an isolated classloader, so the app cannot access OTel API
# classes directly); span IDs for those examples are visible in their container
# logs (OTEL_TRACES_EXPORTER=logging).

set -euo pipefail

N="${1:-40}"
AGENT_URL="http://localhost:8080/fibonacci?n=${N}"
AGENT_CP_URL="http://localhost:8081/fibonacci?n=${N}"
CP_URL="http://localhost:8082/fibonacci?n=${N}"

echo "Sending requests to all examples (Ctrl-C to stop)..."
echo ""

while true; do
    agent=$(curl -sf "${AGENT_URL}" 2>/dev/null || echo "error: spring-opentelemetry-agent-example not reachable")
    agent_cp=$(curl -sf "${AGENT_CP_URL}" 2>/dev/null || echo "error: pring-opentelemetry-agent-classpath-example not reachable")
    cp=$(curl -sf "${CP_URL}" 2>/dev/null || echo "error: spring-opentelemetry-classpath-example not reachable")

    printf "[spring-opentelemetry-agent          ] %s\n" "${agent}"
    printf "[spring-opentelemetry-agent-classpath] %s\n" "${agent_cp}"
    printf "[spring-opentelemetry-classpath      ] %s\n" "${cp}"
    echo ""

    sleep 1
done
