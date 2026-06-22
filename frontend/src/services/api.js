import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
});

export const berthApi = {
  list: () => api.get('/berth-records').then(res => res.data),
  getById: (id) => api.get(`/berth-records/${id}`).then(res => res.data),
  create: (record) => api.post('/berth-records', record).then(res => res.data)
};

export const invoiceApi = {
  generate: (berthRecordId) =>
    api.post(`/invoices/generate/${berthRecordId}`).then(res => res.data),
  list: () => api.get('/invoices').then(res => res.data),
  getById: (id) => api.get(`/invoices/${id}`).then(res => res.data)
};
