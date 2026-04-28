const alerts = [
  { text: "Food budget 85% used", type: "warning" },
  { text: "EMI due in 2 days", type: "danger" },
  { text: "Savings increased this month 🎉", type: "success" },
];

export default function AlertsPanel() {
  return (
    <article className="dashboard-card alerts-card">
      <h2>Alerts</h2>

      <div className="alerts-list">
        {alerts.map((a, i) => (
          <div
            key={i}
            className={`alert-item alert-item--${a.type}`}
          >
            {a.text}
          </div>
        ))}
      </div>
    </article>
  );
}
