import React, { useState } from 'react';
import PropTypes from 'prop-types';

function FormPage({ handleFormSubmit }) {
  const [url, setUrl] = useState('');
  const [summary, setSummary] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log('Form submitted with URL:', url); // Debugging log
    const result = await handleFormSubmit(url);
    setSummary(result);
  };

  return (
    <div>
      <form onSubmit={handleSubmit}>
        <label>
          Website URL: <input 
            type="url" 
            value={url}
            onChange={(e) => setUrl(e.target.value)} 
            required
          />
        </label>
        <button type="submit">Summarize</button>
      </form>
      {summary && (
        <div>
          <h2>Summary:</h2>
          <p>{summary}</p>
        </div>
      )}
    </div>
  );
}

FormPage.propTypes = {
  handleFormSubmit: PropTypes.func.isRequired,
};

export default FormPage;
