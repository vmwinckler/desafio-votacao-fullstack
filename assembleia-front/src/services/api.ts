import axios from 'axios';

// Usar o baseURL do proxy no Vite para dev, e o da prop in prod
const API_URL = '/api/v1';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export interface Agenda {
    id: number;
    title: string;
    description: string;
}

export interface VotingSession {
    id: number;
    agenda: Agenda;
    startTime: string;
    endTime: string;
    open: boolean;
}

export interface VotingResult {
    sessionId: number;
    totalSim: number;
    totalNao: number;
    totalVotes: number;
}

export const agendaService = {
    getAll: async () => {
        const { data } = await api.get<Agenda[]>('/agendas');
        return data;
    },
    getById: async (id: number) => {
        const { data } = await api.get<Agenda>(`/agendas/${id}`);
        return data;
    },
    create: async (title: string, description: string) => {
        const { data } = await api.post<Agenda>('/agendas', { title, description });
        return data;
    },
};

export const sessionService = {
    openSession: async (agendaId: number, durationMinutes: number = 1) => {
        const { data } = await api.post<VotingSession>('/sessions', { agendaId, durationMinutes });
        return data;
    },
    getByAgenda: async (agendaId: number) => {
        try {
            const { data } = await api.get<VotingSession>(`/sessions/agenda/${agendaId}`);
            return data;
        } catch {
            return null;
        }
    },
    getById: async (id: number) => {
        const { data } = await api.get<VotingSession>(`/sessions/${id}`);
        return data;
    }
};

export const voteService = {
    registerVote: async (sessionId: number, memberId: string, choice: 'SIM' | 'NAO') => {
        await api.post('/votes', { sessionId, memberId, choice });
    },
    getResult: async (sessionId: number) => {
        const { data } = await api.get<VotingResult>(`/votes/session/${sessionId}/result`);
        return data;
    }
};
