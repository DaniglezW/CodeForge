import React, { useState } from "react";
import "./../assets/styles/home.css";
import { fetchJpaToJson, fetchJpaToSql, fetchSqlToJpa, fetchSqlToJson } from "../services/formatApi";
import Spinner from "../components/Spinner";
import { toast } from "react-toastify";
import { FaClipboard } from "react-icons/fa";
import { SQLOperation } from "../constants/SqlOperation";
import { cleanJpaModel } from "../utils/Utils";

const Home = () => {
  const [loading, setLoading] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [inputValue, setInputValue] = useState("");
  const [inputFormat, setInputFormat] = useState("");
  const [outputFormat, setOutputFormat] = useState("");
  const [convertedText, setConvertedText] = useState("");
  const [conversionOptions, setConversionOptions] = useState([]);
  const [sqlOperation, setSqlOperation] = useState(SQLOperation.SELECT);

  const handleInputChange = (e) => {
    const selectedInput = e.target.value;
    setInputFormat(selectedInput);

    switch (selectedInput) {
      case "jpa":
        setConversionOptions(["SQL", "JSON"]);
        break;
      case "sql":
        setConversionOptions(["JPA", "JSON"]);
        break;
      default:
        setConversionOptions([]);
    }
    setOutputFormat("");
  };

  const handleOutputChange = (e) => {
    setOutputFormat(e.target.value);
  };

  const handleInputValueChange = (e) => {
    setInputValue(e.target.value);
  };

  const handleFormat = () => {
    setLoading(true);
    if (inputFormat === "sql" && outputFormat === "JPA") {
      fetchSqlToJpa(inputValue)
        .then((data) => {
          setConvertedText(data.response);
          setShowModal(true);
        })
        .catch(() => errorToast())
        .finally(() => setLoading(false));
    } else if (inputFormat === "jpa" && outputFormat === "SQL") {
      const cleanedInputValue = cleanJpaModel(inputValue);

      fetchJpaToSql(cleanedInputValue, sqlOperation)
        .then((data) => {
          setConvertedText(data.response);
          setShowModal(true);
        })
        .catch(() => errorToast())
        .finally(() => setLoading(false));
    } else if (inputFormat === "sql" && outputFormat === "JSON") {
      fetchSqlToJson(inputValue, sqlOperation)
        .then((data) => {
          setConvertedText(data.response);
          setShowModal(true);
        })
        .catch(() => errorToast())
        .finally(() => setLoading(false));
    }  else if (inputFormat === "jpa" && outputFormat === "JSON") {
      fetchJpaToJson(inputValue, sqlOperation)
        .then((data) => {
          setConvertedText(data.response);
          setShowModal(true);
        })
        .catch(() => errorToast())
        .finally(() => setLoading(false));
    }
  };

  const errorToast = () => {
    toast.error('Error en el formato inesperado', {
      position: "top-right",
      autoClose: 2000,
      hideProgressBar: false,
      closeOnClick: false,
      pauseOnHover: true,
      draggable: true,
      progress: undefined,
      theme: "colored",
    });
  }

  const copyToClipboard = () => {
    navigator.clipboard.writeText(convertedText).then(() => {
      toast.success('Copiado al portapapeles!', {
        position: "top-right",
        autoClose: 1000,
        hideProgressBar: false,
        closeOnClick: false,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    }).catch((err) => {
      console.error("Error al copiar: ", err);
    });
  };

  const getFileExtension = (format) => {
    switch (format) {
      case "SQL":
        return "sql"
      case "JSON":
        return "json"
      case "JAVA":
        return "java"
      default:
        return "java"
    }
  };

  const showSqlOptions = () => {
    return (inputFormat === "jpa" && outputFormat === "SQL") || (inputFormat === "sql" && outputFormat === "JSON");
  }

  const cleanApp = () => {
    setInputFormat("");
    setOutputFormat("");
    setInputValue("");
    setConvertedText("");
    setShowModal(false);
  }

  return (
    <div className="home-container">
      <h1>Formateador de Archivos</h1>
      <p>Selecciona el formato de entrada y el formato de salida para convertir.</p>

      <div className="select-container">
        <label htmlFor="input-format">Formato de Entrada</label>
        <select
          id="input-format"
          value={inputFormat}
          onChange={handleInputChange}
          className="select"
        >
          <option value="">Selecciona el formato de entrada</option>
          <option value="jpa">JPA</option>
          <option value="sql">SQL</option>
        </select>
      </div>

      <div className="select-container">
        <label htmlFor="output-format">Formato de Salida</label>
        <select
          id="output-format"
          value={outputFormat}
          onChange={handleOutputChange}
          className="select"
          disabled={!inputFormat}
        >
          <option value="">Selecciona el formato de salida</option>
          {Array.isArray(conversionOptions) && conversionOptions.length > 0 ? (
            conversionOptions.map((option, index) => (
              <option key={index} value={option}>
                {option}
              </option>
            ))
          ) : (
            <option disabled>No hay opciones disponibles</option>
          )}
        </select>

        {showSqlOptions() && (
          <div className="select-container">
            <label htmlFor="sql-operation">Operación SQL</label>
            <select
              id="sql-operation"
              value={sqlOperation}
              onChange={(e) => setSqlOperation(e.target.value)}
              className="select"
            >
              <option value={SQLOperation.SELECT}>{SQLOperation.SELECT}</option>
              <option value={SQLOperation.INSERT}>{SQLOperation.INSERT}</option>
              <option value={SQLOperation.UPDATE}>{SQLOperation.UPDATE}</option>
              <option value={SQLOperation.DELETE}>{SQLOperation.DELETE}</option>
              <option value={SQLOperation.CREATE}>{SQLOperation.CREATE}</option>
            </select>
          </div>
        )}

      </div>

      {inputFormat && outputFormat && (
        <div className="input-container">
          <label htmlFor="input-value">
            Introduzca valor {inputFormat}
          </label>
          <textarea
            id="input-value"
            placeholder={`Introduce el contenido en formato ${inputFormat}`}
            value={inputValue}
            onChange={handleInputValueChange}
            className="input-field"
          />
        </div>
      )}

      <div className="container-btn">
        {inputFormat && outputFormat && inputValue && (
          <button disabled={loading} className="format-button" onClick={handleFormat}>
            {loading ? <Spinner /> : "Formatear"}
          </button>
        )}

        <button className="format-button" onClick={cleanApp}>
          Limpiar
        </button>
      </div>


      {showModal && (
        <div className="modal">
          <div className="modal-content">
            <button className="close-button" onClick={() => setShowModal(false)}>
              X
            </button>
            <h2>Modelo JPA Generado:</h2>

            <div className="code-container">
              <pre className="code-block">{convertedText}</pre>
              <button className="copy-button" onClick={copyToClipboard}><FaClipboard /></button>
            </div>

            <div className="container-btn">
              <button
                className="format-button"
                onClick={() => {
                  const blob = new Blob([convertedText], { type: `application/${getFileExtension(outputFormat)}` });
                  const url = window.URL.createObjectURL(blob);
                  const a = document.createElement("a");
                  a.href = url;
                  a.download = `File.${getFileExtension(outputFormat)}`;
                  document.body.appendChild(a);
                  a.click();
                  document.body.removeChild(a);
                }}
              >
                Descargar File.{getFileExtension(outputFormat)}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Home;