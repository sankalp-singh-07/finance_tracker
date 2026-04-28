const data = [
  { label: "Income", value: "₹45,000", tone: "success" },
  { label: "Expenses", value: "₹30,000", tone: "danger" },
  { label: "Savings", value: "₹15,000", tone: "info" },
  { label: "Health Score", value: "72/100", tone: "warning" },
];

export default function SummaryCards() {
  return (
    <section className="summary-grid">
      {data.map((item, idx) => (
        <div
          key={idx}
          className={`dashboard-card summary-card summary-card--${item.tone}`}
        >
          <p>{item.label}</p>
          <h2>{item.value}</h2>
        </div>
      ))}
    </section>
  );
}
