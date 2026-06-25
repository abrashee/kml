FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:stable-alpine
RUN apk upgrade --no-cache \
    && addgroup -S app \
    && adduser -S -G app app
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/admin/browser /usr/share/nginx/html
RUN chown -R app:app /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]