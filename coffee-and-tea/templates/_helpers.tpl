{{/*
Expand the name of the chart.
*/}}
{{- define "coffee-and-tea.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "coffee-and-tea.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "coffee-and-tea.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "coffee-and-tea.labels" -}}
helm.sh/chart: {{ include "coffee-and-tea.chart" . }}
{{ include "coffee-and-tea.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/component: backend
app.kubernetes.io/part-of: coffee-and-tea-system
{{- end }}

{{/*
Selector labels
*/}}
{{- define "coffee-and-tea.selectorLabels" -}}
app.kubernetes.io/name: {{ include "coffee-and-tea.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "coffee-and-tea.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "coffee-and-tea.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Istio Gateway name
*/}}
{{- define "coffee-and-tea.gatewayName" -}}
{{- if .Values.istio.gateway.name }}
{{- .Values.istio.gateway.name }}
{{- else }}
{{- printf "%s-gateway" (include "coffee-and-tea.fullname" .) }}
{{- end }}
{{- end }}

{{/*
Istio VirtualService name
*/}}
{{- define "coffee-and-tea.virtualServiceName" -}}
{{- printf "%s-vs" (include "coffee-and-tea.fullname" .) }}
{{- end }}

{{/*
Istio DestinationRule name
*/}}
{{- define "coffee-and-tea.destinationRuleName" -}}
{{- printf "%s-dr" (include "coffee-and-tea.fullname" .) }}
{{- end }}

{{/*
PostgreSQL connection string
*/}}
{{- define "coffee-and-tea.postgresql.connectionString" -}}
{{- if .Values.postgresql.enabled }}
{{- printf "jdbc:postgresql://%s-postgresql:5432/%s" (include "coffee-and-tea.fullname" .) .Values.postgresql.auth.database }}
{{- else }}
{{- printf "jdbc:postgresql://external-postgres:5432/CoffeeAndTea" }}
{{- end }}
{{- end }}

{{/*
Kafka bootstrap servers
*/}}
{{- define "coffee-and-tea.kafka.bootstrapServers" -}}
{{- if .Values.kafka.enabled }}
{{- printf "%s-kafka:9092" (include "coffee-and-tea.fullname" .) }}
{{- else }}
{{- printf "external-kafka:9092" }}
{{- end }}
{{- end }}
