const assets = [
  { label: "Gold", value: "₹1,20,000" },
  { label: "Bank", value: "₹80,000" },
  { label: "Cash", value: "₹10,000" },
];

export default function AssetsCard() {
  return (
    <article className="dashboard-card assets-card">
      <h2>Assets</h2>

      <div className="assets-list">
        {assets.map((a, i) => (
          <div key={i} className="asset-row">
            <span>{a.label}</span>
            <strong>{a.value}</strong>
          </div>
        ))}

        <div className="asset-row asset-row--total">
          <span>Net Worth</span>
          <strong>₹2,10,000</strong>
        </div>
      </div>
    </article>
  );
}
