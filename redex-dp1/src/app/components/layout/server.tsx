import express, { Request, Response } from 'express';
import mysql from 'mysql';
import cors from 'cors';

const app = express();
const port = 3001;

app.use(cors());

const db = mysql.createConnection({
  host: 'db-redex-1a.c01nmehbjhju.us-east-1.rds.amazonaws.com',
  user: 'admin',
  password: 'adaviladp1',
  database: 'Sergio'
});

db.connect((err) => {
  if (err) {
    console.error('Error connecting to the database:', err);
    return;
  }
  console.log('Connected to the database');
});

app.get('/search/:id', (req: Request, res: Response) => {
  const idPaquete = req.params.id;
  const query = `SELECT vuelos FROM paquete_dto WHERE IdPaquete = ?`;

  db.query(query, [idPaquete], (err, results) => {
    if (err) {
      console.error('Error executing query:', err);
      res.status(500).send('Error executing query');
      return;
    }
    if (results.length === 0) {
      res.status(404).send('No data found');
    } else {
      res.json(results);
    }
  });
});

app.listen(port, () => {
  console.log(`Server running on port ${port}`);
});