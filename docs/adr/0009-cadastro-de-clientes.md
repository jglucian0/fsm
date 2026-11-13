# ADR 0009 — Cadastro de clientes
**Status:** aceito

**Contexto:** o orçamento (Fase 2b) e o envio por WhatsApp/e-mail (Fase 3) precisam de um cliente com contato. O endereço servirá ao cálculo da taxa de deslocamento.

**Decisão:**
- `Cliente(id, ownerId, nome, telefone, email, endereco)`, imutável (record), criado por `Cliente.novo(...)`. Módulo `clientes`, com exceção própria (`ClienteInvalidoException`): módulos não compartilham o `core`.
- `nome` obrigatório, aparado, até 120 caracteres. **Não é único**: duas pessoas podem se chamar igual.
- `telefone`, `email` e `endereco` opcionais; em branco vale como ausente (`null`).
- `telefone` guardado só com dígitos (DDD + número = 10 ou 11), aceitando o prefixo `55`. Isso serve direto ao link `wa.me` da Fase 3.
- `email` aparado e em minúsculas, validado de forma simples (algo@dominio.ext); validação forte fica para o envio real.
- `endereco` texto livre, aparado, até 255 caracteres. Geocodificação para o deslocamento fica para a Fase 2c.
- Fase 2a: cadastrar e listar. Editar e remover ficam para depois (remover cliente com orçamento exige regra própria).

**Consequências:** o orçamento referenciará o cliente só por `clienteId`, sem importar o `core` de `clientes` (regra de módulos).
