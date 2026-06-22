import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { invoiceApi } from '../services/api';
import './InvoiceDetail.css';

export default function InvoiceDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [invoice, setInvoice] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadInvoice = async () => {
      setLoading(true);
      setError('');
      try {
        const data = await invoiceApi.getById(id);
        setInvoice(data);
      } catch (e) {
        const msg = e.response?.data?.message || e.message;
        setError('加载账单失败: ' + msg);
      } finally {
        setLoading(false);
      }
    };
    loadInvoice();
  }, [id]);

  if (loading) {
    return <div className="page"><p>加载中...</p></div>;
  }

  if (error) {
    return (
      <div className="page">
        <p className="error-text">{error}</p>
        <button className="btn btn-default" onClick={() => navigate('/')}>返回列表</button>
      </div>
    );
  }

  if (!invoice) {
    return (
      <div className="page">
        <p>账单不存在</p>
        <button className="btn btn-default" onClick={() => navigate('/')}>返回列表</button>
      </div>
    );
  }

  return (
    <div className="page">
      <h1 className="page-title">账单详情</h1>

      <div className="invoice-card">
        <div className="invoice-header">
          <span className="invoice-status">{invoice.status}</span>
          <span className="invoice-no">账单编号: {invoice.id}</span>
        </div>

        <div className="invoice-section">
          <h3>船舶信息</h3>
          <div className="detail-grid">
            <div className="detail-item">
              <label>船名</label>
              <span>{invoice.shipName}</span>
            </div>
            <div className="detail-item">
              <label>吨位</label>
              <span>{invoice.tonnage} 吨</span>
            </div>
            <div className="detail-item">
              <label>靠泊时长</label>
              <span>{invoice.berthDurationHours} 小时</span>
            </div>
            <div className="detail-item">
              <label>费率类型</label>
              <span>{invoice.rateType}</span>
            </div>
            <div className="detail-item">
              <label>靠泊记录ID</label>
              <span>{invoice.berthRecordId}</span>
            </div>
            <div className="detail-item">
              <label>创建时间</label>
              <span>{invoice.createTime}</span>
            </div>
          </div>
        </div>

        <div className="invoice-section">
          <h3>费用明细</h3>
          <div className="detail-grid">
            <div className="detail-item">
              <label>单价(元/小时)</label>
              <span className="amount">{invoice.unitPrice}</span>
            </div>
            <div className="detail-item">
              <label>靠泊时长(小时)</label>
              <span>{invoice.berthDurationHours}</span>
            </div>
            <div className="detail-item highlight">
              <label>应付金额</label>
              <span className="amount total">¥ {invoice.totalAmount}</span>
            </div>
          </div>
        </div>
      </div>

      <div className="toolbar">
        <button className="btn btn-default" onClick={() => navigate('/')}>返回靠泊记录列表</button>
      </div>
    </div>
  );
}
