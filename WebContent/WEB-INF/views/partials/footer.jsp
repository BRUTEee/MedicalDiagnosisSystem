        </div>

    </main>

</div>
<script nonce="<%= View.esc((String) request.getAttribute("cspNonce")) %>">
    document.querySelectorAll("form[data-confirm]").forEach(function (form) {
        form.addEventListener("submit", function (event) {
            if (!window.confirm(form.dataset.confirm)) {
                event.preventDefault();
            }
        });
    });
</script>
</body>
</html>
