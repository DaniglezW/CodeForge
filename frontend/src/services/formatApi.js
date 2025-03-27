const API_BASE_URL = "http://localhost:8080/api/code-forge/v1/format";

export const fetchSqlToJpa = async (sqlQuery) => {
  const response = await fetch(API_BASE_URL + '/sql-to-jpa', {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ sqlQuery }),
  });

  if (!response.ok) throw new Error("Error en la conversión");

  const data = await response.json();
  return {
    jpaModel: data.jpaModel,
    fileContent: new Uint8Array(data.fileContent),
  };
};
