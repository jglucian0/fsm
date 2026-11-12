-- Fase 0: baseline.
-- btree_gist será usada na Fase 4 para a constraint de exclusão anti double-booking
-- (EXCLUDE USING gist (owner_id WITH =, periodo WITH &&)).
CREATE EXTENSION IF NOT EXISTS btree_gist;
