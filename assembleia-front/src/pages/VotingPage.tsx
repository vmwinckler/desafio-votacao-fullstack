import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Clock, ThumbsUp, ThumbsDown, AlertCircle } from 'lucide-react';
import { sessionService, voteService, VotingSession } from '../services/api';

export default function VotingPage() {
    const { agendaId } = useParams<{ agendaId: string }>();
    const navigate = useNavigate();

    const [session, setSession] = useState<VotingSession | null>(null);
    const [loading, setLoading] = useState(true);
    const [timeLeft, setTimeLeft] = useState<string>('');
    const [cpf, setCpf] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        async function loadSession() {
            if (!agendaId) return;
            try {
                const data = await sessionService.getByAgenda(Number(agendaId));
                if (!data || !data.open) {
                    navigate('/');
                    return;
                }
                setSession(data);
            } catch (err) {
                navigate('/');
            } finally {
                setLoading(false);
            }
        }
        loadSession();
    }, [agendaId, navigate]);

    useEffect(() => {
        if (!session) return;

        const interval = setInterval(() => {
            const end = new Date(session.endTime).getTime();
            const now = new Date().getTime();
            const distance = end - now;

            if (distance < 0) {
                clearInterval(interval);
                navigate(`/result/${session.id}`);
                return;
            }

            const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((distance % (1000 * 60)) / 1000);
            setTimeLeft(`${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`);
        }, 1000);

        return () => clearInterval(interval);
    }, [session, navigate]);

    const handleVote = async (choice: 'SIM' | 'NAO') => {
        if (!cpf || cpf.length < 11) {
            setError('Por favor, informe um CPF válido.');
            return;
        }
        if (!session) return;

        setError('');
        setIsSubmitting(true);
        try {
            await voteService.registerVote(session.id, cpf, choice);
            alert('Voto registrado com sucesso!');
            navigate('/');
        } catch (err: any) {
            console.error("Detalhamento do erro de Voto:", err);

            if (err.response && err.response.data && err.response.data.message) {
                setError(err.response.data.message);
            } else if (err.message) {
                setError(`Erro de conexão: Não foi possível alcançar o servidor.`);
            } else {
                setError('Erro desconhecido ao registrar o voto. Tente novamente.');
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    if (loading || !session) {
        return (
            <div className="flex justify-center items-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>
        );
    }

    return (
        <div className="max-w-3xl mx-auto">
            <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden mb-6">
                <div className="p-6 border-b border-gray-100 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                    <div>
                        <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-1">Pauta em Votação</h2>
                        <h1 className="text-2xl font-bold text-gray-900">{session.agenda.title}</h1>
                    </div>

                    <div className="bg-amber-50 text-amber-700 px-4 py-3 rounded-lg border border-amber-100 flex items-center gap-2 font-mono text-lg font-semibold shrink-0">
                        <Clock size={20} className="text-amber-600" />
                        {timeLeft || '00:00'}
                    </div>
                </div>

                <div className="p-6 bg-gray-50 text-gray-700">
                    <p>{session.agenda.description}</p>
                </div>
            </div>

            <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-6">
                <h3 className="text-lg font-bold text-gray-900 mb-4 text-center">Registre seu Voto</h3>

                <div className="max-w-md mx-auto">
                    <div className="mb-6">
                        <label htmlFor="cpf" className="block text-sm font-medium text-gray-700 mb-2 text-center">
                            Informe seu CPF (Apenas números)
                        </label>
                        <input
                            type="text"
                            id="cpf"
                            maxLength={11}
                            value={cpf}
                            onChange={(e) => setCpf(e.target.value.replace(/\D/g, ''))}
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition text-center text-lg tracking-widest font-mono"
                            placeholder="00000000000"
                        />
                    </div>

                    {error && (
                        <div className="mb-6 bg-red-50 text-red-600 p-4 rounded-lg flex items-start gap-2 text-sm">
                            <AlertCircle size={18} className="shrink-0 mt-0.5" />
                            <span>{error}</span>
                        </div>
                    )}

                    <div className="grid grid-cols-2 gap-4">
                        <button
                            onClick={() => handleVote('SIM')}
                            disabled={isSubmitting || !cpf}
                            className="group relative h-24 bg-white border-2 border-green-500 rounded-xl flex flex-col items-center justify-center text-green-600 hover:bg-green-50 focus:ring-4 focus:ring-green-100 transition disabled:opacity-50 disabled:cursor-not-allowed overflow-hidden"
                        >
                            <ThumbsUp size={32} className="mb-2 group-hover:scale-110 transition-transform" />
                            <span className="font-bold text-lg leading-none">SIM</span>
                        </button>

                        <button
                            onClick={() => handleVote('NAO')}
                            disabled={isSubmitting || !cpf}
                            className="group relative h-24 bg-white border-2 border-red-500 rounded-xl flex flex-col items-center justify-center text-red-600 hover:bg-red-50 focus:ring-4 focus:ring-red-100 transition disabled:opacity-50 disabled:cursor-not-allowed overflow-hidden"
                        >
                            <ThumbsDown size={32} className="mb-2 group-hover:scale-110 transition-transform" />
                            <span className="font-bold text-lg leading-none">NÃO</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
