# Issue #20 — Validar que o médico atende na clínica da consulta

## Regra
Ao **agendar** uma consulta, valida-se que `appointment.clinic().id()` está contido em
`doctor.clinicIds()`. Caso o médico não atenda na clínica informada, lança-se
`DoctorNotAffiliatedToClinicException` e a consulta **não é persistida**.

## Status HTTP escolhido: `409 CONFLICT`
Optou-se por `409` (e não `422`) para manter consistência com as demais violações de
regra de negócio do agendamento já mapeadas no projeto
(`DoctorScheduleConflictException`, `DoctorTimeSlotTakenException`,
`OutsideDoctorScheduleException`, `AppointmentNotReschedulableException`), todas tratadas
como conflito de estado do recurso. A requisição é sintaticamente válida; o conflito é a
incompatibilidade entre o vínculo médico↔clínica e o recurso solicitado.

Mapeada em ambos os `@RestControllerAdvice` existentes
(`AppointmentExceptionHandler` e `GlobalExceptionHandler`), no grupo de conflitos.

## Reagendamento
O reagendamento (`ReagendarConsultaCaseImpl`) altera **apenas a data** — o par
médico↔clínica é imutável e já foi validado no momento do agendamento. Portanto não há
revalidação de vínculo no reagendamento (seria redundante).

## Arquivos
- `core/exceptions/DoctorNotAffiliatedToClinicException.java` (novo)
- `core/usecases/appointment/AgendarConsultaCaseImpl.java` (validação)
- `infrastructure/controller/handler/AppointmentExceptionHandler.java` (mapeamento 409)
- `infrastructure/handler/GlobalExceptionHandler.java` (mapeamento 409)
- Testes: `AgendarConsultaCaseImplTest` (vínculo válido e inválido)
