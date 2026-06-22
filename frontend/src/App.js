import React from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import BerthRecordList from './pages/BerthRecordList';
import InvoiceDetail from './pages/InvoiceDetail';
import './App.css';

export default function App() {
  return (
    <div className="app">
      <header className="app-header">
        <Link to="/" className="app-logo">码头靠泊费结算系统</Link>
      </header>
      <main className="app-main">
        <Routes>
          <Route path="/" element={<BerthRecordList />} />
          <Route path="/invoices/:id" element={<InvoiceDetail />} />
        </Routes>
      </main>
    </div>
  );
}
