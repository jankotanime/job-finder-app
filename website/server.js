import express from 'express';
import path from 'path';
import { fileURLToPath } from 'url';

const app = express();
const port = 3000;

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);


app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(express.static(path.join(__dirname, 'public')));

app.get('/', async (req, res) => {
  res.render('index.ejs');
});

app.get('/about-us', async (req, res) => {
  res.render('aboutUs.ejs');
});

app.get('/download', async (req, res) => {
  res.render('download.ejs');
});

app.get('/faq', async (req, res) => {
  res.render('faq.ejs');
});

app.get('/licences', async (req, res) => {
  res.render('licences.ejs');
});

app.get('/update-password', async (req, res) => {
  const apiUrl = process.env.SSR_API_URL;
  res.render('updatePassword.ejs', { apiUrl });
});

app.get('/update-password-success', async (req, res) => {
  res.render('updatePasswordSuccess.ejs');
});

app.listen(port, () => {
  console.log(`Server listening on http://localhost:${port}`);
});