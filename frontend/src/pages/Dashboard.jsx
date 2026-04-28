import SummaryCards from "../components/SummaryCards.jsx";
import ExpensePieChart from "../components/ExpensePieChart";
import MonthlyBarChart from "../components/MonthlyBarChart";
import AlertsPanel from "../components/AlertPanel";
import AssetsCard from "../components/AssetsCard";
import QuickActions from "../components/QuickActions.jsx";
import "./Dashboard.css";
import "../components/DashboardComponents.css";

export default function Dashboard() {
  return (
    <main className="dashboard">
      <div className="dashboard__shell">
        <nav className="dashboard__nav">
          <a className="dashboard__brand" href="/">
            <span className="dashboard__brand-mark">F</span>
            <span>FinSight</span>
          </a>

          <div className="dashboard__nav-links" aria-label="Dashboard navigation">
            <a href="/">Overview</a>
            <a href="/">Budgets</a>
            <a href="/">Reports</a>
          </div>

          <button className="dashboard__period" type="button">
            This Month
          </button>
        </nav>

        <header className="dashboard__header">
          <div>
            <p className="dashboard__eyebrow">Personal finance</p>
            <h1>Financial Dashboard</h1>
            <p className="dashboard__intro">
              Track monthly spending, assets, savings, and alerts from one
              clean overview.
            </p>
          </div>

          <div className="dashboard__profile">
            <span>Welcome back</span>
            <strong>User</strong>
            <small>Health score 72/100</small>
          </div>
        </header>

        <SummaryCards />

        <section className="dashboard__grid dashboard__grid--charts">
          <ExpensePieChart />
          <MonthlyBarChart />
        </section>

        <section className="dashboard__section">
          <AlertsPanel />
        </section>

        <section className="dashboard__grid dashboard__grid--bottom">
          <AssetsCard />
          <QuickActions />
        </section>
      </div>
    </main>
  );
}
