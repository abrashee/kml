FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:stable-alpine
RUN addgroup -S app \
    && adduser -S -G app app
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
RUN chown -R app:app /usr/share/nginx/html \
    && chown -R app:app /var/cache/nginx /var/log/nginx /etc/nginx/conf.d \
    && touch /var/run/nginx.pid \
    && chown app:app /var/run/nginx.pid
USER app
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
