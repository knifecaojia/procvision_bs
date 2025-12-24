# 阶段1：构建Vue静态资源
FROM node:lts-alpine AS build-stage

WORKDIR /app

RUN addgroup --system appgroup \
    && adduser --system --ingroup appgroup --home /home/appuser appuser \
    && chown -R appuser:appgroup /app \
    && chmod -R 755 /app

COPY package.json ./

RUN yarn --registry=https://registry.npmmirror.com

RUN yarn --registry=https://registry.npmmirror.com \
    && chmod a+x /app/node_modules/.bin/vite \
    && chmod a+x $(readlink -f /app/node_modules/.bin/vite) \
    && chown -R appuser:appgroup /app/node_modules

COPY . .
RUN chown -R appuser:appgroup /app \
    && chmod -R 755 /app

USER appuser

# 构建生产环境包（若有环境变量，可通过--build-arg传递）
RUN /app/node_modules/.bin/vite build

# 阶段2：部署到Nginx
FROM nginx AS production-stage
# 复制构建好的静态资源到Nginx默认目录
COPY --from=build-stage /app/dist /usr/share/nginx/html
# 暴露端口
EXPOSE 80
# 启动Nginx
CMD ["nginx", "-g", "daemon off;"]