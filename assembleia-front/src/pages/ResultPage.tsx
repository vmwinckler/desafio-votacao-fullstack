import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, CheckCircle2, XCircle } from 'lucide-react';
import { sessionService, voteService, VotingSession, VotingResult } from '../services/api';

export default function ResultPage() {
    const { sessionId } = useParams<{ sessionId: string }>();
    const [session, setSession] = useState<VotingSession | null>(null);
    const [result, setResult] = useState<VotingResult | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function fetchResult() {
            if (!sessionId) return;
            try {
                const sessionData = await sessionService.getById(Number(sessionId));
                setSession(sessionData);

                const resultData = await voteService.getResult(Number(sessionId));
                setResult(resultData);
            } catch (err) {
                console.error("Erro ao carregar resultados", err);
            } finally {
                setLoading(false);
            }
        }
        fetchResult();
    }, [sessionId]);

    if (loading || !session || !result) {
        return (
            <div className="flex justify-center items-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>
        );
    }

    const { totalSim, totalNao, totalVotes } = result;

    const simPercentage = totalVotes > 0 ? Math.round((totalSim / totalVotes) * 100) : 0;
    const naoPercentage = totalVotes > 0 ? Math.round((totalNao / totalVotes) * 100) : 0;

    const isApproved = totalSim > totalNao;
    const isTie = totalSim === totalNao && totalVotes > 0;

    return (
        <div className="max-w-3xl mx-auto">
            <Link to="/" className="inline-flex items-center text-sm font-medium text-gray-500 hover:text-gray-700 mb-6 transition">
                <ArrowLeft size={16} className="mr-1" />
                Voltar para Pautas
            </Link>

            <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden mb-6">
                <div className="p-6 border-b border-gray-100 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                    <div>
                        <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-1">Resultado da Pauta</h2>
                        <h1 className="text-2xl font-bold text-gray-900">{session.agenda.title}</h1>
                    </div>

                    <div className={`px-4 py-2 rounded-lg flex items-center gap-2 font-bold text-lg shrink-0 ${totalVotes === 0 ? 'bg-gray-100 text-gray-600' :
                            isTie ? 'bg-amber-100 text-amber-700' :
                                isApproved ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                        }`}>
                        {totalVotes === 0 ? (
                            <span>Sem votos</span>
                        ) : isTie ? (
                            <span>Empate</span>
                        ) : isApproved ? (
                            <><CheckCircle2 size={24} /> Pauta Aprovada</>
                        ) : (
                            <><XCircle size={24} /> Pauta Rejeitada</>
                        )}
                    </div>
                </div>

                <div className="p-6 bg-gray-50 text-gray-700">
                    <p>{session.agenda.description}</p>
                </div>
            </div>

            <div className="grid md:grid-cols-2 gap-6">
                <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-6 flex flex-col items-center justify-center">
                    <span className="text-gray-500 font-medium mb-2">Total de Votos</span>
                    <span className="text-5xl font-black text-blue-600 mb-1">{totalVotes}</span>
                    <span className="text-sm text-gray-400">associados participaram</span>
                </div>

                <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-6 space-y-6">
                    <div>
                        <div className="flex justify-between items-end mb-2">
                            <span className="font-bold text-gray-700 flex items-center gap-2">SIM</span>
                            <div className="text-right">
                                <span className="text-2xl font-black text-green-600">{totalSim}</span>
                                <span className="text-gray-500 text-sm ml-1">votos ({simPercentage}%)</span>
                            </div>
                        </div>
                        <div className="w-full bg-gray-100 rounded-full h-3 overflow-hidden">
                            <div className="bg-green-500 h-3 rounded-full transition-all duration-1000" style={{ width: `${simPercentage}%` }}></div>
                        </div>
                    </div>

                    <div>
                        <div className="flex justify-between items-end mb-2">
                            <span className="font-bold text-gray-700 flex items-center gap-2">NÃO</span>
                            <div className="text-right">
                                <span className="text-2xl font-black text-red-600">{totalNao}</span>
                                <span className="text-gray-500 text-sm ml-1">votos ({naoPercentage}%)</span>
                            </div>
                        </div>
                        <div className="w-full bg-gray-100 rounded-full h-3 overflow-hidden">
                            <div className="bg-red-500 h-3 rounded-full transition-all duration-1000" style={{ width: `${naoPercentage}%` }}></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
