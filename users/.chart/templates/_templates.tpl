{{- define "users-backend.deployment" }}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ .appName }}-deployment
  namespace: {{ .namespace }}
  labels:
    app: {{ .appName }}
spec:
  replicas: {{ .replicasCount }}
  progressDeadlineSeconds: 120
  selector:
    matchLabels:
      app: {{ .appName }}
  template:
    metadata:
      labels:
        app: {{ .appName }}
    spec:
      containers:
      - name: {{ .appName }}
        image: {{ .container.image }}:{{ .container.version }}
        imagePullPolicy: Always
{{- if .container.volumeMounts }}
        volumeMounts:
        - mountPath: {{ .container.volumeMounts.mountPath }}
          name: {{ .container.volumeMounts.name }}
{{- end }}
{{- if .container.command }}
        command: {{ .container.command | quote }}
{{- end }}
{{- if .container.args }}
        args: {{ .container.args | quote }}
{{- end }}
        ports:
        - containerPort: {{ .container.port }}
        resources:
            requests:
                memory: 256M
                cpu: 50m
            limits:
                memory: 512M
                cpu: 200m
{{- if .container.env }}
        env:
{{ toYaml .container.env | indent 8 }}
{{- end }}
{{- if .container.volumeMounts }}
      volumes:
        - name: {{ .container.volumeMounts.name }}
{{- end }}
{{- end }}

{{- define "users-backend.service" }}
{{- if .service }}
apiVersion: v1
kind: Service
metadata:
  name: {{ .appName }}-service
  namespace: {{ .namespace }}
spec:
  selector:
    app: {{ .appName }}
  {{ if .service.type  }}type: {{ .service.type }}{{ end }}
  ports:
  - name:
    protocol: TCP
    port: {{ .service.port}}
    targetPort: {{ .service.targetPort }}
    {{ if .service.nodePort }}nodePort: {{ .service.nodePort }} {{ end }}
{{- end }}
{{- end }}