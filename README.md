# Exposed: false-positive default changes in MigrationUtils (PostgreSQL)

Reproduces two bugs in `SchemaUtilityApi.dbDefaultToString()` that make
`MigrationUtils.statementsRequiredForDatabaseMigration()` report `SET DEFAULT` statements for tables
that were just created from the very same Exposed definition.

Exposed 1.6.0, PostgreSQL 18.1 (Testcontainers, Docker required), JDK 21.

```shell
./gradlew test --continue
```

Each test creates a table with `SchemaUtils.create()` and expects that no migration statements are required afterwards.

## `jsonb-default`

`jsonb` defaults whose JSON contains boolean or number values. PostgreSQL normalizes the default to
`'{"open": false}'::jsonb`, but Exposed only adds the space after `:` and `,` when a string,
object or array follows.

| Test | Result |
|---|---|
| default with boolean value | fails: `ALTER TABLE devices_boolean_default ALTER COLUMN status SET DEFAULT '{"open":false}'::jsonb` |
| default with number value | fails: `ALTER TABLE devices_number_default ALTER COLUMN counter SET DEFAULT '{"name":"alarms","count":0}'::jsonb` |
| default with only string and array values (control) | passes |

## `transform-default`

`varchar(...).transform(...).default(...)`. PostgreSQL reports `'Europe/Berlin'::character varying`, but Exposed
renders the transformed default without the cast because neither the wrapped value (`ZoneId`) nor the column type
(`ColumnWithTransform`) is recognized as a string/varchar.

| Test | Result |
|---|---|
| transformed varchar default | fails: `ALTER TABLE devices_transformed_default ALTER COLUMN timezone SET DEFAULT 'Europe/Berlin'` |
| plain varchar default (control) | passes |
