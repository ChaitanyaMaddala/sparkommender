// Freelancer Theme JavaScript

(function($) {
    "use strict"; // Start of use strict

    // jQuery for page scrolling feature - requires jQuery Easing plugin
    $('.page-scroll a').bind('click', function(event) {
        var $anchor = $(this);
        $('html, body').stop().animate({
            scrollTop: ($($anchor.attr('href')).offset().top - 50)
        }, 1250, 'easeInOutExpo');
        event.preventDefault();
    });

    // Highlight the top nav as scrolling occurs
    $('body').scrollspy({
        target: '.navbar-fixed-top',
        offset: 51
    });

    // Closes the Responsive Menu on Menu Item Click
    $('.navbar-collapse ul li a:not(.dropdown-toggle)').click(function() {
        $('.navbar-toggle:visible').click();
    });

    // Offset for Main Navigation
    $('#mainNav').affix({
        offset: {
            top: 100
        }
    })

    // Floating label headings for the contact form
    $(function() {
        $("body").on("input propertychange", ".floating-label-form-group", function(e) {
            $(this).toggleClass("floating-label-form-group-with-value", !!$(e.target).val());
        }).on("focus", ".floating-label-form-group", function() {
            $(this).addClass("floating-label-form-group-with-focus");
        }).on("blur", ".floating-label-form-group", function() {
            $(this).removeClass("floating-label-form-group-with-focus");
        });
    });


	// Get the form.
	var form = $('#recommendForm');

	// Get the messages div.
	var formMessages = $('#form-messages');

	var results = $('#results');

    function toMap(parameters) {
      var result = {};
      parameters.split("&").forEach(function(part) {
        var item = part.split("=");
        if(item[1] != "") result[item[0]] = decodeURIComponent(item[1]);
      });
      return result;
    }

	// Set up an event listener for the contact form.
	$(form).submit(function(e) {

        $(results).hide();
		// Stop the browser from submitting the form.
		e.preventDefault();

		// Serialize the form data.
		var formData = toMap($(form).serialize());

        var user = ""
        if("user" in formData) {
            user = "?user=" + formData.user;
        }
        var recommendUrl = "/api/models/" + formData.model + "/destinations/" + formData.destination + user

		// Submit the form using AJAX.
		$.ajax({
		    //accepts: 'application/json',
			type: 'GET',
			//url: $(form).attr('action'),
            url: recommendUrl
		})
		.done(function(response) {

			// Make sure that the formMessages div has the 'success' class.
			$(formMessages).removeClass('error');
			$(formMessages).addClass('success');

			// Set the message text.
			//here also add hotel Icon?
			$(formMessages).text(response);
			$(results).show();
		})
		.fail(function(data) {
			// Make sure that the formMessages div has the 'error' class.
			$(formMessages).removeClass('success');
			$(formMessages).addClass('error');

			// Set the message text.
			if (data.responseText !== '') {
				$(formMessages).text(data.responseText);
			} else {
				$(formMessages).text('Oops! An error occured and your message could not be sent.');
			}
		});

	});

})(jQuery); // End of use strict
