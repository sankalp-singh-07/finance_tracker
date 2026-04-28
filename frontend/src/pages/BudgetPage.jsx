import { useEffect, useMemo, useState } from "react";
import "./Dashboard.css";
import "./BudgetPage.css";

const DEMO_BUDGETS_KEY = "finance-tracker-demo-budgets";

const FALLBACK_CATEGORIES = [
  { id: 4, name: "Food", type: "EXPENSE", defaultCategory: true },
  { id: 5, name: "Rent", type: "EXPENSE", defaultCategory: true },
  { id: 6, name: "Utilities", type: "EXPENSE", defaultCategory: true },
  { id: 7, name: "Transport", type: "EXPENSE", defaultCategory: true },
  { id: 8, name: "Health", type: "EXPENSE", defaultCategory: true },
];

function getCurrentMonth() {
  return new Date().toISOString().slice(0, 7);
}

function getStoredDemoBudgets(month) {
  try {
    const stored = JSON.parse(localStorage.getItem(DEMO_BUDGETS_KEY)) || [];
    return stored.filter((budget) => budget.month === month);
  } catch {
    return [];
  }
}

function saveStoredDemoBudget(nextBudget) {
  const stored = JSON.parse(localStorage.getItem(DEMO_BUDGETS_KEY) || "[]");
  const withoutDuplicate = stored.filter(
    (budget) =>
      !(
        budget.month === nextBudget.month &&
        Number(budget.category?.id) === Number(nextBudget.category?.id)
      )
  );

  localStorage.setItem(
    DEMO_BUDGETS_KEY,
    JSON.stringify([...withoutDuplicate, nextBudget])
  );
}

function formatCurrency(value) {
  return new Intl.NumberFormat("en-IN", {
    currency: "INR",
    maximumFractionDigits: 0,
    style: "currency",
  }).format(Number(value || 0));
}

async function requestJson(path, token, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  });

  if (!response.ok) {
    throw new Error("Request failed");
  }

  return response.json();
}

export default function BudgetPage({ onLogout, onNavigate, user }) {
  const token = user?.token || localStorage.getItem("finance-tracker-token");
  const [month, setMonth] = useState(getCurrentMonth);
  const [categories, setCategories] = useState(FALLBACK_CATEGORIES);
  const [budgets, setBudgets] = useState([]);
  const [form, setForm] = useState({
    categoryId: String(FALLBACK_CATEGORIES[0].id),
    monthlyLimit: "",
  });
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    let active = true;

    async function loadBudgetPage() {
      setLoading(true);
      setMessage("");

      if (!token) {
        const demoBudgets = getStoredDemoBudgets(month);
        if (active) {
          setCategories(FALLBACK_CATEGORIES);
          setBudgets(demoBudgets);
          setLoading(false);
          setMessage("Demo mode: budgets are saved in this browser.");
        }
        return;
      }

      try {
        const [categoryData, budgetData] = await Promise.all([
          requestJson("/api/v1/categories", token),
          requestJson(`/api/v1/budgets?month=${month}`, token),
        ]);

        if (!active) return;

        const expenseCategories = categoryData.filter(
          (category) => category.type === "EXPENSE"
        );

        setCategories(
          expenseCategories.length ? expenseCategories : FALLBACK_CATEGORIES
        );
        setBudgets(budgetData);
      } catch {
        if (active) {
          setCategories(FALLBACK_CATEGORIES);
          setBudgets([]);
          setMessage(
            "Start the backend or sign in with a backend account to sync budgets."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadBudgetPage();

    return () => {
      active = false;
    };
  }, [month, token]);

  const totals = useMemo(() => {
    return budgets.reduce(
      (current, budget) => ({
        limit: current.limit + Number(budget.monthlyLimit || 0),
        remaining: current.remaining + Number(budget.remainingAmount || 0),
        used: current.used + Number(budget.usedAmount || 0),
      }),
      { limit: 0, remaining: 0, used: 0 }
    );
  }, [budgets]);

  const selectedCategoryId = form.categoryId || String(categories[0]?.id || "");

  function updateForm(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (!selectedCategoryId || Number(form.monthlyLimit) <= 0 || saving) return;

    setSaving(true);
    setMessage("");

    const selectedCategory = categories.find(
      (category) => Number(category.id) === Number(selectedCategoryId)
    );

    try {
      if (token) {
        const savedBudget = await requestJson("/api/v1/budgets", token, {
          method: "POST",
          body: JSON.stringify({
            categoryId: Number(selectedCategoryId),
            monthlyLimit: form.monthlyLimit,
            month,
          }),
        });

        setBudgets((current) => [
          ...current.filter(
            (budget) =>
              Number(budget.category?.id) !== Number(savedBudget.category?.id)
          ),
          savedBudget,
        ]);
        setMessage("Budget saved to backend.");
      } else {
        const monthlyLimit = Number(form.monthlyLimit);
        const demoBudget = {
          id: `${month}-${selectedCategoryId}`,
          month,
          monthlyLimit,
          usedAmount: 0,
          remainingAmount: monthlyLimit,
          category: selectedCategory,
        };

        saveStoredDemoBudget(demoBudget);
        setBudgets(getStoredDemoBudgets(month));
        setMessage("Demo budget saved in this browser.");
      }

      setForm((current) => ({ ...current, monthlyLimit: "" }));
    } catch {
      setMessage("Could not save budget. Check backend login and try again.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="dashboard budget-page">
      <div className="dashboard__shell">
        <nav className="dashboard__nav">
          <button
            className="dashboard__brand dashboard__brand-button"
            onClick={() => onNavigate("dashboard")}
            type="button"
          >
            <span className="dashboard__brand-mark">F</span>
            <span>FinSight</span>
          </button>

          <div className="dashboard__nav-links" aria-label="Dashboard navigation">
            <button onClick={() => onNavigate("dashboard")} type="button">
              Overview
            </button>
            <button className="is-active" type="button">
              Budgets
            </button>
            <button type="button">Reports</button>
          </div>

          <label className="budget-month">
            <span>Month</span>
            <input
              onChange={(event) => setMonth(event.target.value)}
              type="month"
              value={month}
            />
          </label>

          <button className="dashboard__logout" onClick={onLogout} type="button">
            Logout
          </button>
        </nav>

        <header className="dashboard__header budget-hero">
          <div>
            <p className="dashboard__eyebrow">Budget planning</p>
            <h1>Monthly Budgets</h1>
            <p className="dashboard__intro">
              Set category limits, compare spending with remaining balance, and
              keep every month aligned with your backend budget records.
            </p>
          </div>

          <div className="dashboard__profile">
            <span>Signed in as</span>
            <strong>{user?.name || "User"}</strong>
            <small>{token ? "Backend sync on" : "Demo mode"}</small>
          </div>
        </header>

        <section className="budget-summary" aria-label="Budget summary">
          <article>
            <span>Total limit</span>
            <strong>{formatCurrency(totals.limit)}</strong>
          </article>
          <article>
            <span>Used</span>
            <strong>{formatCurrency(totals.used)}</strong>
          </article>
          <article>
            <span>Remaining</span>
            <strong>{formatCurrency(totals.remaining)}</strong>
          </article>
        </section>

        <section className="budget-layout">
          <form className="budget-form" onSubmit={handleSubmit}>
            <div>
              <p className="budget-section-label">Set budget</p>
              <h2>Category limit</h2>
            </div>

            <label>
              <span>Expense category</span>
              <select
                name="categoryId"
                onChange={updateForm}
                value={selectedCategoryId}
              >
                {categories.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>
            </label>

            <label>
              <span>Monthly limit</span>
              <input
                min="1"
                name="monthlyLimit"
                onChange={updateForm}
                placeholder="25000"
                step="0.01"
                type="number"
                value={form.monthlyLimit}
              />
            </label>

            <button
              disabled={
                saving || !selectedCategoryId || Number(form.monthlyLimit) <= 0
              }
              type="submit"
            >
              {saving ? "Saving..." : "Save budget"}
            </button>

            {message && <p className="budget-message">{message}</p>}
          </form>

          <section className="budget-list" aria-label="Budget list">
            <div className="budget-list__header">
              <div>
                <p className="budget-section-label">Active budgets</p>
                <h2>{month}</h2>
              </div>
              <span>{loading ? "Loading" : `${budgets.length} categories`}</span>
            </div>

            {budgets.length ? (
              budgets.map((budget) => {
                const limit = Number(budget.monthlyLimit || 0);
                const used = Number(budget.usedAmount || 0);
                const percent = limit > 0 ? Math.min((used / limit) * 100, 100) : 0;
                const isOver = used > limit;

                return (
                  <article
                    className="budget-item"
                    key={budget.id || budget.category?.id}
                  >
                    <div className="budget-item__top">
                      <div>
                        <h3>{budget.category?.name || "Category"}</h3>
                        <p>{isOver ? "Over budget" : "Within budget"}</p>
                      </div>
                      <strong>{formatCurrency(budget.monthlyLimit)}</strong>
                    </div>

                    <div
                      className="budget-progress"
                      aria-label={`${budget.category?.name} used amount`}
                    >
                      <span style={{ width: `${percent}%` }} />
                    </div>

                    <div className="budget-item__meta">
                      <span>Used {formatCurrency(budget.usedAmount)}</span>
                      <span>Remaining {formatCurrency(budget.remainingAmount)}</span>
                    </div>
                  </article>
                );
              })
            ) : (
              <div className="budget-empty">
                <h3>No budgets yet</h3>
                <p>Add a category limit to start tracking this month.</p>
              </div>
            )}
          </section>
        </section>
      </div>
    </main>
  );
}
