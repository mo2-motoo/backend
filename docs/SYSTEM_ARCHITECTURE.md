# System Architecture

This document provides an overview of the system architecture of the Motoo backend project. The architecture is based on a microservices pattern, with various components for the application, database,
caching, monitoring, and logging.

## System Architecture Diagram

(An image of the system architecture will be provided separately.)

## Component Descriptions

The system is composed of several services defined in the `docker-compose.yml`, `docker-compose.monitoring.yml`, and `docker-compose.logging.yml` files.

### Application Services (`docker-compose.yml`)

- **`backend`**: The main application service, built from the Dockerfile in the project root. It depends on the `db` and `redis` services and runs on port 8080.
- **`db`**: A PostgreSQL database service (version 15) that serves as the primary data store. It exposes port 5432 and stores data in a volume named `postgres_data`.
- **`redis`**: A Redis service (version 7) used for caching and as a message queue. It exposes port 6379 and persists data in a volume named `redis_data`.
- **`redis-cli`**: A Redis client service for interacting with the Redis server, primarily for debugging and manual operations.

### Monitoring Services (`docker-compose.monitoring.yml`)

- **`prometheus`**: A monitoring and alerting toolkit that scrapes metrics from various exporters. It runs on port 9090.
- **`grafana`**: A visualization and analytics platform used to create dashboards for monitoring metrics collected by Prometheus and logs from Loki. It runs on port 3001.
- **`cadvisor`**: A container advisor that collects, aggregates, processes, and exports information about running containers. It runs on port 8081.
- **`postgres-exporter`**: An exporter that scrapes metrics from the PostgreSQL database for Prometheus.
- **`k6`**: A load testing tool for performance testing the application.
- **`node-exporter`**: An exporter for hardware and OS metrics exposed by *NIX kernels.
- **`redis-exporter`**: An exporter that scrapes Redis metrics for Prometheus.

### Logging Services (`docker-compose.logging.yml`)

- **`loki`**: A log aggregation system designed to store and query logs from all services. It runs on port 3100.
- **`promtail`**: An agent that ships the contents of local logs to a private Loki instance.
- **`grafana`**: The same Grafana instance from the monitoring setup, also used to visualize logs from Loki. It runs on port 3000.

