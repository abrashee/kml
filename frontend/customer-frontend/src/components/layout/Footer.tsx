// src / components / Footer.tsx
export default function Footer() {
  return (
    <footer className="footer">
      <div className="layout-canvas">
        <div className="footer-grid">
          <div>
            <h4>KML Logistics</h4>
            <p style={{ opacity: 0.6, pointerEvents: "none" }}>
              Automated global freight orchestration and network distribution engine.
            </p>
          </div>

          <div>
            <h4>Operations</h4>
            <p>API Integration Docs</p>
          </div>

          <div>
            <h4>Operations</h4>
            <p>Operator Help Desk</p>
          </div>

        </div>

        <div className="footer-bottom">
          © {new Date().getFullYear()} KML Logistics Inc. All server tracking connections encrypted.
        </div>
      </div>
    </footer>
  );
}
