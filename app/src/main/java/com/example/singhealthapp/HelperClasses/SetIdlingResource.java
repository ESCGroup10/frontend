package com.example.singhealthapp.HelperClasses;

import androidx.annotation.Nullable;

public class SetIdlingResource {
    SimpleIdlingResource idlingResource;

    public SetIdlingResource(@Nullable final SimpleIdlingResource simpleIdlingResource) {
        this.idlingResource = simpleIdlingResource;
    }

    public void toFalse() {
        if (idlingResource != null) {
            System.out.println("setting idling resource to false");
            idlingResource.setIdleState(false);
        }
    }

    public void toTrue() {
        if (idlingResource != null) {
            System.out.println("setting idling resource to true");
            idlingResource.setIdleState(true);
        }
    }

}
