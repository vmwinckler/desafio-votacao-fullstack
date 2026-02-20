import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { PlayCircle, CheckCircle2 } from 'lucide-react';
import { Agenda, agendaService, sessionService, VotingSession } from '../services/api';

export default function AgendasListPage() {
    const [agendas, setAgendas] = useState<Agenda[]>([]);
    const [sessions, setSessions] = useState<Record<number, VotingSession | null>>({});
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function fetchData() {
            try {
                const data = await agendaService.getAll();
                setAgendas(data);

                const sessionsData: Record<number, VotingSession | null> = {};
                for (const agenda of data) {
                    const session = await sessionService.getByAgenda(agenda.id);
                    sessionsData[agenda.id] = session;
                }
                setSessions(sessionsData);
            } catch (error) {
                console.error("Erro ao carregar pautas:", error);
            } finally {
                setLoading(false);
            }
        }
        fetchData();
    }, []);

    if (loading) {
        return (
            <div className="flex justify-center items-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>
        );
    }

    return (
        <div>
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold text-gray-900">Pautas</h1>
                <Link
                    to="/create-agenda"
                    className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition shadow-sm"
                >
                    Criar Nova Pauta
                </Link>
            </div>

            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
                {agendas.length === 0 ? (
                    <div className="col-span-full bg-white p-8 rounded-xl border border-gray-200 text-center text-gray-500">
                        Nenhuma pauta cadastrada. Crie a primeira!
                    </div>
                ) : (
                    agendas.map(agenda => {
                        const session = sessions[agenda.id];

                        return (
                            <div key={agenda.id} className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden flex flex-col hover:shadow-md transition">
                                <div className="p-6 flex-1">
                                    <h3 className="text-lg font-bold text-gray-900 mb-2">{agenda.title}</h3>
                                    <p className="text-gray-600 text-sm line-clamp-3">{agenda.description}</p>
                                </div>

                                <div className="bg-gray-50 px-6 py-4 border-t border-gray-100 flex items-center justify-between">
                                    {!session ? (
                                        <button
                                            onClick={async () => {
                                                try {
                                                    const newSession = await sessionService.openSession(agenda.id);
                                                    setSessions(prev => ({ ...prev, [agenda.id]: newSession }));
                                                } catch (e) {
                                                    alert("Erro ao abrir sessão");
                                                }
                                            }}
                                            className="text-blue-600 hover:text-blue-800 text-sm font-medium flex items-center gap-1"
                                        >
                                            <PlayCircle size={16} /> Abrir Votação (1 min)
                                        </button>
                                    ) : session.open ? (
                                        <Link
                                            to={`/vote/${agenda.id}`}
                                            className="text-green-600 hover:text-green-800 text-sm font-medium flex items-center gap-1 animate-pulse"
                                        >
                                            Sessão Aberta - Votar Agora
                                        </Link>
                                    ) : (
                                        <Link
                                            to={`/result/${session.id}`}
                                            className="text-gray-600 hover:text-gray-800 text-sm font-medium flex items-center gap-1"
                                        >
                                            <CheckCircle2 size={16} /> Ver Resultados
                                        </Link>
                                    )}
                                </div>
                            </div>
                        );
                    })
                )}
            </div>
        </div>
    );
}
