---
name: ✨ 기능 요청 (Feature Request)
about: 새로운 아이디어나 기능을 제안합니다.
title: "[FEAT] "
labels: enhancement
assignees: ''
---

## 💡 제안 배경 (Motivation)
## 🚀 기능 상세 (Description)
- [ ] 이메일 인증을 통한 비밀번호 재설정
- [ ] 임시 비밀번호 발급 API 구현

## ✅ 완료 조건 (Acceptance Criteria)
- [ ] 사용자는 '비밀번호 찾기' 버튼을 클릭할 수 있다.
- [ ] 등록된 이메일로 인증 코드가 3분 이내에 전송된다.
- [ ] 인증 코드가 일치하면 비밀번호 변경 페이지로 이동한다.

## 🧩 기술적 고려사항 (Technical Details)
- Redis를 사용하여 인증 코드의 TTL(5분) 관리 필요
- Spring MailSender 설정 필요

## 🎨 디자인/참고 자료 (Optional)