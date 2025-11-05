const apiUrl = window.apiUrl;

document.getElementById("updatePassword").addEventListener("submit", function(e) {
    e.preventDefault();
    const password = document.getElementById("password");
    const repeatPassword = document.getElementById("repeatPassword");
    const errorPassword = document.getElementById("errorPassword");
    const errorRepeatPassword = document.getElementById("errorRepeatPassword");
    const errorAuth = document.getElementById("errorAuth");
    const errorServer = document.getElementById("errorServer");

    errorPassword.style.display = "none";
    errorRepeatPassword.style.display = "none";
    errorAuth.style.display = "none";
    errorServer.style.display = "none";

    const hasEightCharacters = password.value.length >= 8;
    const hasNumber = /\d/.test(password.value);
    const hasUppercase = /[A-Z]/.test(password.value);
    const hasLowercase = /[a-z]/.test(password.value);

    if (password.value !== repeatPassword.value) {
      errorRepeatPassword.style.display = "block";
      return;
    }

    if (!hasEightCharacters || !hasNumber || !hasUppercase || !hasLowercase) {
      errorPassword.style.display = "block";
      return;
    }

    const params = new URLSearchParams(window.location.search);
    const tokenId = params.get("token-id");
    const token = params.get("token");

  fetch(`${apiUrl}/password/website/update`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      password: password.value,
      tokenId: tokenId,
      token: token
    })
  }).then(res => {
    if (res.ok) {
      window.location.href = '/update-password-success';
    } else if (res.status === 401) {
      res.json().then(_ => {
        errorAuth.style.display = "block";
      });
    } else {
      res.json().then(_ => {
        errorServer.style.display = "block";
      });
    }
  }).catch(error => {
    console.error("Error:", error);
  });
})