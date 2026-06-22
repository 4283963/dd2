import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { berthApi, invoiceApi } from '../services/api';
import './BerthRecordList.css';

export default function BerthRecordList() {
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [generatingId, setGeneratingId] = useState(null);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    loadRecords();
  }, []);

  const loadRecords = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await berthApi.list();
      setRecords(data);
    } catch (e) {
      setError('加载靠泊记录失败: ' + e.message);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateInvoice = async (berthRecordId) => {
    setGeneratingId(berthRecordId);
    setError('');
    try {
      const invoice = await invoiceApi.generate(berthRecordId);
      navigate(`/invoices/${invoice.id}`);
    } catch (e) {
      const status = e.response?.status;
      const msg = e.response?.data?.message || e.message;
      if (status === 409) {
        setError('该记录已生成过账单，请勿重复生成');
      } else {
        setError('生成账单失败: ' + msg);
      }
    } finally {
      setGeneratingId(null);
    }
  };

  return (
    <div className="page">
      <h1 className="page-title">靠泊记录</h1>

      <div className="toolbar">
        <button className="btn btn-primary" onClick={loadRecords} disabled={loading}>
          {loading ? '加载中...' : '刷新列表'}
        </button>
        {error && <span className="error-text">{error}</span>}
      </div>

      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>船名</th>
            <th>吨位(吨)</th>
            <th>靠泊时长(小时)</th>
            <th>费率类型</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          {records.length === 0 && !loading && (
            <tr>
              <td colSpan="6" className="empty-row">暂无靠泊记录</td>
            </tr>
          )}
          {records.map(record => (
            <tr key={record.id}>
              <td>{record.id}</td>
              <td>{record.shipName}</td>
              <td>{record.tonnage}</td>
              <td>{record.berthDurationHours}</td>
              <td>{record.rateType}</td>
              <td>
                <button
                  className="btn btn-success"
                  onClick={() => handleGenerateInvoice(record.id)}
                  disabled={generatingId === record.id}
                >
                  {generatingId === record.id ? '生成中...' : '生成账单'}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
