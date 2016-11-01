Project Description
    Modify an existing project to add features - See screenshots
    
Bonus Features
    * Feature 1 - Landscape mode
        This is implemented by creating a new layout file in layout-land folder. Copied the
        content of the portrait version then modified it to look better for landscape. Android
        Handled the rest
    * Feature 2 - Team Picture update
        This feature allows the user to update the image for any/both of the teams by tapping
        on the image. This is done by first by adding an onClick listener to both imageViews.
        Then an intent is used to be able to allow the user to pick an image. Once the image 
        is picked, onActivityResult() is overridden which checks the result of the intent, and
        grabs the data (image path).
        In order to know which image to update since there is only one onActivityResult(), when
        the user clicks on one of the images then a string, toBeUpdate, is set to PROFILE_A OR 
        PROFILE_B and based on the value of toBeUpdated, the image for the proper team gets
        updated. Screen rotation is also handled for this, meaning that the profiles won't reset
        to the default ones if the screen is rotated.
        
        Note: Store permission needs to be granted to the app in order for this to work