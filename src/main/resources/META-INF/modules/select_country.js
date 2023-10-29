define( ["jquery"], function ($) {
    $("#userRegion").on("change", function() {
        let option = $("#userRegion option:selected").text();
        if ( option && option.match("РФ") ) {
            $("#countryNameRow").collapse("show")
        }
        else {
            $("#countryNameRow").collapse("hide")
        }
    });
});