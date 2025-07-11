import { Constants } from "../constants/Constants";

const apiUrl = process.env.REACT_APP_API_URL;

export const fetchSqlToJpa = async (sqlQuery) => {
  const response = await fetch(apiUrl + Constants.API_SQL_TO_JPA, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ sqlQuery }),
  });

  if (!response.ok) throw new Error("Error en la conversión");

  const data = await response.json();
  return {
    response: data.response,
    fileContent: new Uint8Array(data.fileContent),
  };
};

export const fetchJpaToSql = async (request, sqlOperationType) => {
  const response = await fetch(apiUrl + Constants.API_JPA_TO_SQL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ request, sqlOperationType }),
  });

  if (!response.ok) throw new Error("Error en la conversión");

  const data = await response.json();
  return {
    response: data.response,
    fileContent: new Uint8Array(data.fileContent),
  };
};

export const fetchSqlToJson = async (request, sqlOperationType) => {
  const response = await fetch(apiUrl + Constants.API_SQL_TO_JSON, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ request, sqlOperationType }),
  });

  if (!response.ok) throw new Error("Error en la conversión");

  const data = await response.json();
  return {
    response: data.response,
    fileContent: new Uint8Array(data.fileContent),
  };
};

export const fetchJpaToJson = async (request, sqlOperationType) => {
  const response = await fetch(apiUrl + Constants.API_JPA_TO_JSON, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ request, sqlOperationType }),
  });

  if (!response.ok) throw new Error("Error en la conversión");

  const data = await response.json();
  return {
    response: data.response,
    fileContent: new Uint8Array(data.fileContent),
  };
};