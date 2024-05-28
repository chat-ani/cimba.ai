import React, { useEffect, useState } from 'react';

const HistoryPage = () => {
  const [history, setHistory] = useState([]);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await fetch('http://127.0.0.1:8000/api/history');
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setHistory(data);
      } catch (error) {
        console.error('Error fetching history:', error);
      }
    };

    fetchHistory();
  }, []);

  return (
    <div>
      <h1>History</h1>
      <ul>
        {history.map((item, index) => (
          <li key={index} className="history-item">
            <p><strong>URL:</strong> {item.url}</p>
            <p><strong>Summary:</strong> {item.summary}</p>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default HistoryPage;
