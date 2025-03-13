const express = require('express');
const mysql = require('mysql');
const bodyParser = require('body-parser');
const cors = require('cors');
const path = require('path'); // Importar el módulo path

const app = express();
app.use(bodyParser.json());
app.use(cors());

// Configuración de la conexión a MySQL
const db = mysql.createConnection({
  host: 'localhost', // Cambia si tu MySQL está en otro servidor
  user: 'root',      // Usuario de MySQL
  password: 'luimi2025',      // Contraseña de MySQL
  database: 'myapp_db' // Nombre de la base de datos
});

// Conectar a MySQL
db.connect((err) => {
  if (err) throw err;
  console.log('Conectado a la base de datos MySQL');
});

// Endpoint para iniciar sesión
app.post('/login', (req, res) => {
  const { email, password } = req.body;
  const query = 'SELECT * FROM users WHERE email = ? AND password = ?';
  db.query(query, [email, password], (err, results) => {
    if (err) {
      return res.status(500).json({ success: false, message: 'Error en el servidor' });
    }
    if (results.length > 0) {
      res.json({ success: true, message: 'Inicio de sesión exitoso', user: results[0] });
    } else {
      res.status(401).json({ success: false, message: 'Credenciales inválidas' });
    }
  });
});

// Endpoint para registrarse
app.post('/register', (req, res) => {
  const { email, password } = req.body;
  const query = 'INSERT INTO users (email, password) VALUES (?, ?)';
  db.query(query, [email, password], (err, results) => {
    if (err) {
      return res.status(500).json({ success: false, message: 'Error en el servidor' });
    }
    res.json({ success: true, message: 'Registro exitoso' });
  });
});

// Servir el archivo HTML en la raíz
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'index.html'));
});
// Iniciar el servidor
const PORT = 3000;
app.listen(PORT, () => {
  console.log(`Servidor backend corriendo en http://localhost:${PORT}`);
});