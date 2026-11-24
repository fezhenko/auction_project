# Tasks: readme-architecture

## 1. Rewrite the README (one commit)

- [ ] 1.1 Write the headline, the Mermaid architecture diagram with outbox, relay, `auction.finished` and `auction.finished.DLT`, and the money-path description. Verify: every arrow in the diagram corresponds to a call, topic or table in the code (check each against the modules).
- [ ] 1.2 Write the delivery-guarantee paragraph using the exact wording from the proposal. Verify: `grep -i "exactly-once\|exactly once\|ordered" README.md` returns nothing.
- [ ] 1.3 Update the tech-stack list with the versions from the poms (`./mvnw help:evaluate` or `dependency:tree`), the running instructions and the environment-variable table. Verify: the versions match the build, and the commands run as written on a clean clone.
- [ ] 1.4 Rewrite "Known limitations" and replace "Roadmap". Verify: each limitation is still true in the code, and no fixed issue is still listed.
- [ ] 1.5 Verify `./mvnw clean verify` is green, then commit.
