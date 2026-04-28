export default function QuickActions({ onNavigate }) {
  return (
    <article className="dashboard-card quick-actions-card">
      <h2>Quick Actions</h2>

      <div className="quick-actions">
        <button className="quick-action quick-action--primary">
          + Transaction
        </button>

        <button
          className="quick-action quick-action--success"
          onClick={() => onNavigate("budgets")}
          type="button"
        >
          + Budget
        </button>

        <button className="quick-action quick-action--warning">
          + EMI
        </button>

        <button className="quick-action quick-action--accent">
          + Asset
        </button>
      </div>
    </article>
  );
}
