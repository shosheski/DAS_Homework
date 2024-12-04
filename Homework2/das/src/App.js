import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './Home';
import List from './List';

function App() {
    const [state, setState] = useState([]);

    useEffect(() => {
        fetch('http://localhost:8080/api/all')
            .then((response) => response.json())
            .then((data) => {
                setState(data);
            })
            .catch((error) => console.error('Error fetching data:', error));
    }, []);

    return (
        <Router>
            <Routes>
                <Route
                    path="/"
                    element={<Home />}
                />
                <Route
                    path="/list"
                    element={
                        <div>
                            <h1>Data from Spring Boot API</h1>
                            {state.map((obj) => (
                                <div key={obj.id}>
                                    <p>Name: {obj.name}</p>
                                    <p>Promet: {obj.promet}</p>
                                </div>
                            ))}
                        </div>
                    }
                />
            </Routes>
        </Router>
    );
}

export default App;
