import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { agendaService } from '../services/api';

export default function CreateAgendaPage() {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!title.trim() || !description.trim()) return;

        setIsSubmitting(true);
        try {
            await agendaService.create(title, description);
            navigate('/');
        } catch (error: any) {
            alert("Erro ao criar pauta: " + (error.response?.data?.message || error.message));
            setIsSubmitting(false);
        }
    };

    return (
        <div className="max-w-2xl mx-auto">
            <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
                <div className="px-6 py-5 border-b border-gray-100 bg-gray-50">
                    <h2 className="text-xl font-bold text-gray-900">Nova Pauta</h2>
                    <p className="text-sm text-gray-500 mt-1">Crie uma nova pauta para ser votada pelos associados.</p>
                </div>

                <form onSubmit={handleSubmit} className="p-6 space-y-6">
                    <div>
                        <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-1">
                            Título da Pauta
                        </label>
                        <input
                            type="text"
                            id="title"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                            placeholder="Ex: Aprovação de Contas 2023"
                            required
                        />
                    </div>

                    <div>
                        <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-1">
                            Descrição
                        </label>
                        <textarea
                            id="description"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            rows={4}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition resize-none"
                            placeholder="Descreva os detalhes da pauta..."
                            required
                        />
                    </div>

                    <div className="flex justify-end gap-3 pt-4 border-t border-gray-100">
                        <button
                            type="button"
                            onClick={() => navigate('/')}
                            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition"
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition shadow-sm"
                        >
                            {isSubmitting ? 'Salvando...' : 'Salvar Pauta'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
