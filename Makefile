# 변수로 설정해두면 명령어가 깔끔해집니다
COMPOSE_BASE := -f docker-compose.yml
COMPOSE_DEV  := --env-file .env.dev -f docker-compose.dev.yml
COMPOSE_PROD := --env-file .env.prod -f docker-compose.prod.yml

# 1. 개발 환경 실행 (DB, Redis만 실행)
dev:
	docker compose $(COMPOSE_BASE) $(COMPOSE_DEV) up -d

# 2. 개발 환경 종료
dev-down:
	docker compose $(COMPOSE_BASE) $(COMPOSE_DEV) down

# 3. 운영 환경 실행 (DB, Redis + Spring 서버 빌드 및 실행)
prod:
	docker compose $(COMPOSE_BASE) $(COMPOSE_PROD) up -d --build

# 4. 운영 환경 종료
prod-down:
	docker compose $(COMPOSE_BASE) $(COMPOSE_PROD) down

# 5. 모든 데이터 삭제하고 초기화 (볼륨 삭제)
clean:
	docker compose $(COMPOSE_BASE) $(COMPOSE_DEV) down -v
	docker compose $(COMPOSE_BASE) $(COMPOSE_PROD) down -v

# 6. 실행 중인 dev 컨테이너 로그 보기
dev-logs:
	docker compose $(COMPOSE_BASE) $(COMPOSE_DEV) logs -f

# 7. 실행 중인 prod 컨테이너 로그 보기
prod-logs:
	docker compose $(COMPOSE_BASE) $(COMPOSE_PROD) logs -f