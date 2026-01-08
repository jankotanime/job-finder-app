const apiUrl = window.apiUrl;

document.getElementById("updatePassword").addEventListener("submit", function(e) {
    e.preventDefault();
    const password = document.getElementById("password");
    const repeatPassword = document.getElementById("repeatPassword");
    const errorPassword = document.getElementById("errorPassword");

    const hasEightCharacters = password.value.length >= 8;
    const hasNumber = /\d/.test(password.value);
    const hasUppercase = /[A-Z]/.test(password.value);
    const hasLowercase = /[a-z]/.test(password.value);

    if (password.value !== repeatPassword.value) {
      errorPassword.textContent = "Hasła nie są takie same!";
      return;
    }

    if (!hasEightCharacters) {
      errorPassword.textContent = "Hasło powinno zawierać osiem znaków!";
      return;
    }

    if (!hasNumber) {
      errorPassword.textContent = "Hasło powinno zawierać conajmniej jedną cyfrę!";
      return;
    }

    if (!hasUppercase) {
      errorPassword.textContent = "Hasło powinno zawierać conajmniej jedną dużą literę!";
      return;
    }

    if (!hasLowercase) {
      errorPassword.textContent = "Hasło powinno zawierać conajmniej jedną małą literę!";
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
        errorPassword.textContent = "Problem z autentykacją: Proszę spróbować ponownie wysłać maila do resetowania haseł.";
      });
    } else {
      res.json().then(_ => {
        errorPassword.textContent = "Wystąpił nieoczekiwany problem, proszę skontaktować się z nami na: majami.technology@gmail.com";
      });
    }
  }).catch(_ => {
    errorPassword.textContent = "Wystąpił nieoczekiwany problem, proszę skontaktować się z nami na: majami.technology@gmail.com";
  });
})