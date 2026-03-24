/* ========================================================
   SGO - auth.js  |  Authentication & Session Management
   ======================================================== */

'use strict';

const TOKEN_KEY = 'sgo_token';
const USER_KEY  = 'sgo_user';

/* ── Token ── */
function getToken() {
  return sessionStorage.getItem(TOKEN_KEY);
}

/* ── Current User ── */
function getCurrentUser() {
  const raw = sessionStorage.getItem(USER_KEY);
  if (!raw) return null;
  try { return JSON.parse(raw); }
  catch (_) { return null; }
}

/* ── Redirect by role ── */
function redirectByRole(user) {
  if (!user) { window.location.href = 'index.html'; return; }
  switch (user.role) {
    case 'MANAGER':   window.location.href = 'dashboard.html'; break;
    case 'RECEPTION': window.location.href = 'agenda.html';    break;
    case 'MECHANIC':  window.location.href = 'mecanico.html';  break;
    default:          window.location.href = 'index.html';
  }
}

/* ── Check auth (call on every protected page) ── */
function checkAuth() {
  if (!getToken()) {
    window.location.href = 'index.html';
    return false;
  }
  return true;
}

/* ── Check role (redirect if insufficient) ── */
function checkRole(requiredRoles) {
  const user = getCurrentUser();
  if (!user) { window.location.href = 'index.html'; return false; }
  if (!requiredRoles.includes(user.role)) {
    redirectByRole(user);
    return false;
  }
  return true;
}

/* ── Login ── */
async function login(username, password) {
  const resp = await api.login({ username, password });
  // Backend may return token directly or nested
  const token = resp.token || resp.accessToken;
  const user  = resp.user  || resp;
  if (!token) throw new Error('Token não recebido do servidor.');
  sessionStorage.setItem(TOKEN_KEY, token);
  // If user data is embedded in response root (no .user key)
  const userToStore = resp.user || {
    id:       resp.id,
    username: resp.username,
    name:     resp.name,
    role:     resp.role,
  };
  sessionStorage.setItem(USER_KEY, JSON.stringify(userToStore));
  return userToStore;
}

/* ── Logout ── */
function logout() {
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(USER_KEY);
  window.location.href = 'index.html';
}

/* ── Page init helper for all protected pages ── */
function initProtectedPage(requiredRoles) {
  if (!checkAuth()) return false;
  if (requiredRoles && !checkRole(requiredRoles)) return false;
  populateSidebarUser();
  setActiveSidebarLink();
  initMobileSidebar();
  initThemeToggle();
  return true;
}
