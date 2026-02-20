import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import AgendasListPage from './pages/AgendasListPage';
import CreateAgendaPage from './pages/CreateAgendaPage';
import VotingPage from './pages/VotingPage';
import ResultPage from './pages/ResultPage';

function App() {
    return (
        <Router>
            <Layout>
                <Routes>
                    <Route path="/" element={<AgendasListPage />} />
                    <Route path="/create-agenda" element={<CreateAgendaPage />} />
                    <Route path="/vote/:agendaId" element={<VotingPage />} />
                    <Route path="/result/:sessionId" element={<ResultPage />} />
                </Routes>
            </Layout>
        </Router>
    );
}

export default App;
