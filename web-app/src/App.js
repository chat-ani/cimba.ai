import React, { useState } from 'react';
import FormPage from './components/FormPage';
import HistoryPage from './components/HistoryPage';
import './App.css'; // Import the CSS file

function App() {
  const [currentPage, setCurrentPage] = useState('form');
  const [history, setHistory] = useState([]);

  const handleFormSubmit = async (url) => {
    try {
      console.log('Submitting URL:', url); // Debugging log
      const response = await fetch('http://127.0.0.1:8000/api/summarize/', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ url }),
      });

      console.log('Response received:', response); // Debugging log

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      const newEntry = { url, summary: data.summary };
      setHistory([newEntry, ...history]);
      return data.summary;
    } catch (error) {
      console.error('Error:', error);
      return 'Failed to fetch summary';
    }
  };

  return (
    <div>
      <header>
        <h1>URL Summarizer</h1>
      </header>
      <div className="container">
        <button onClick={() => setCurrentPage('form')}>Summary</button>
        <button onClick={() => setCurrentPage('history')}>History</button>
        {currentPage === 'form' && <FormPage handleFormSubmit={handleFormSubmit} />}
        {currentPage === 'history' && <HistoryPage history={history} />}
      </div>
    </div>
  );
}

export default App;
