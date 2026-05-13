package main

import (
	"context"
	"log/slog"
	"os"

	"go.opentelemetry.io/contrib/bridges/otelslog"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/exporters/otlp/otlplog/otlploghttp"
	"go.opentelemetry.io/otel/exporters/otlp/otlpmetric/otlpmetrichttp"
	"go.opentelemetry.io/otel/exporters/otlp/otlptrace/otlptracehttp"
	"go.opentelemetry.io/otel/log/global"
	sdklog "go.opentelemetry.io/otel/sdk/log"
	sdkmetric "go.opentelemetry.io/otel/sdk/metric"
	"go.opentelemetry.io/otel/sdk/resource"
	sdktrace "go.opentelemetry.io/otel/sdk/trace"
	semconv "go.opentelemetry.io/otel/semconv/v1.26.0"
)

func serviceName() string {
	if n := os.Getenv("OTEL_SERVICE_NAME"); n != "" {
		return n
	}
	return "click-stream-processor"
}

// setupTelemetry initialises traces, metrics and logs.
// Call the returned shutdown func on exit.
func setupTelemetry(ctx context.Context) func(context.Context) {
	res, _ := resource.Merge(
		resource.Default(),
		resource.NewWithAttributes(
			semconv.SchemaURL,
			semconv.ServiceName(serviceName()),
			semconv.ServiceNamespace(os.Getenv("OTEL_SERVICE_NAMESPACE")),
		),
	)

	// Traces
	traceExp, err := otlptracehttp.New(ctx)
	if err != nil {
		slog.Error("failed to create trace exporter", "error", err)
	} else {
		tp := sdktrace.NewTracerProvider(
			sdktrace.WithBatcher(traceExp),
			sdktrace.WithResource(res),
		)
		otel.SetTracerProvider(tp)
	}

	// Metrics
	metricExp, err := otlpmetrichttp.New(ctx)
	if err != nil {
		slog.Error("failed to create metric exporter", "error", err)
	} else {
		mp := sdkmetric.NewMeterProvider(
			sdkmetric.WithReader(sdkmetric.NewPeriodicReader(metricExp)),
			sdkmetric.WithResource(res),
		)
		otel.SetMeterProvider(mp)
	}

	// Logs — slog bridged to OTel, no monkey-patching
	logExp, err := otlploghttp.New(ctx)
	if err != nil {
		slog.Error("failed to create log exporter", "error", err)
	} else {
		lp := sdklog.NewLoggerProvider(
			sdklog.WithProcessor(sdklog.NewBatchProcessor(logExp)),
			sdklog.WithResource(res),
		)
		global.SetLoggerProvider(lp)
		slog.SetDefault(slog.New(otelslog.NewHandler(serviceName())))
	}

	return func(ctx context.Context) {
		if tp, ok := otel.GetTracerProvider().(*sdktrace.TracerProvider); ok {
			tp.Shutdown(ctx)
		}
		if mp, ok := otel.GetMeterProvider().(*sdkmetric.MeterProvider); ok {
			mp.Shutdown(ctx)
		}
		if lp, ok := global.GetLoggerProvider().(*sdklog.LoggerProvider); ok {
			lp.Shutdown(ctx)
		}
	}
}
