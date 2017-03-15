-- name: get-service-config-sql
SELECT keys.key_path as key, conf_value as val, keys.parser
  FROM conf.conf_service_values
  JOIN conf.keys ON keys.key_path = conf_service_values.key_path
  JOIN conf.conf_values ON conf_values.id = conf_service_values.value_id
 WHERE conf_service_values.service = :service
 ORDER BY keys.key_path

--name: get-service-secret-sql
SELECT secret
  FROM conf.service_secrets
  WHERE service = :service
