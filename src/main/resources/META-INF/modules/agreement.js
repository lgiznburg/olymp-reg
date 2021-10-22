define( ["jquery"], function ($) {

    $("#agreementBox").on("click", function (){
        if ( $(this).prop("checked") ) {
            $("#signupSubmit").attr("disabled", false)
        }
        else {
            $("#signupSubmit").attr("disabled", true)
        }
    });
});