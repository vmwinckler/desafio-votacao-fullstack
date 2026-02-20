import { ReactNode } from 'react';
import { Link } from 'react-router-dom';
import { LayoutDashboard, PlusCircle } from 'lucide-react';

export default function Layout({ children }: { children: ReactNode }) {
    return (
        <div className="min-h-screen bg-gray-50 flex flex-col">
            <header className="bg-white border-b border-gray-200 sticky top-0 z-10 shadow-sm">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16 items-center">
                        <div className="flex items-center">
                            <Link to="/" className="flex items-center gap-2 group">
                                <div className="bg-blue-600 text-white p-2 rounded-lg group-hover:bg-blue-700 transition">
                                    <LayoutDashboard size={24} />
                                </div>
                                <span className="font-bold text-xl text-gray-900 tracking-tight">Votação Cooperativa</span>
                            </Link>
                        </div>
                        <nav className="flex items-center gap-4">
                            <Link
                                to="/create-agenda"
                                className="flex items-center gap-2 text-sm font-medium text-gray-600 hover:text-blue-600 transition"
                            >
                                <PlusCircle size={18} />
                                Nova Pauta
                            </Link>
                        </nav>
                    </div>
                </div>
            </header>

            <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {children}
            </main>

            <footer className="bg-white border-t border-gray-200 py-6 mt-auto">
                <div className="max-w-7xl mx-auto px-4 text-center text-sm text-gray-500">
                    Assembleia Digital © {new Date().getFullYear()}
                </div>
            </footer>
        </div>
    );
}
