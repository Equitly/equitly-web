// Landing overlay fade effect
document.addEventListener('DOMContentLoaded', function() {
    const landingOverlay = document.getElementById('landingOverlay');
    const body = document.body;
    
    // Initially hide body content
    body.style.overflow = 'hidden';
    
    // After 3 seconds, fade out the overlay
    setTimeout(function() {
        landingOverlay.style.opacity = '0';
        landingOverlay.style.transition = 'opacity 1s ease-out';
        
        // After fade completes, hide overlay and show content
        setTimeout(function() {
            landingOverlay.style.display = 'none';
            body.style.overflow = 'auto';
        }, 1000);
    }, 3000);
    
    // Allow user to scroll to dismiss early
    let scrollTimeout;
    window.addEventListener('scroll', function() {
        if (landingOverlay.style.display !== 'none') {
            clearTimeout(scrollTimeout);
            scrollTimeout = setTimeout(function() {
                landingOverlay.style.opacity = '0';
                landingOverlay.style.transition = 'opacity 0.5s ease-out';
                setTimeout(function() {
                    landingOverlay.style.display = 'none';
                    body.style.overflow = 'auto';
                }, 500);
            }, 100);
        }
    });
});

